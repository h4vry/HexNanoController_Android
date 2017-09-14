/********************************************************************  
filename:	RTMPStream.h 
created:	2013-04-3 
author: 	firehood  
*********************************************************************/
#pragma once  


#if 0
#if defined __UINT32_MAX__ or UINT32_MAX
#include <inttypes.h>
#else
typedef unsigned char uint8_t;
typedef unsigned short uint16_t;
//typedef unsigned long uint32_t;
typedef unsigned long long uint64_t;
#endif
#endif
#define __STDC_INT64__

#include <stdint.h>
//#include <cstdint>

#include <stdio.h>	
  
#include "vmcdef.h"

//#define FILEBUFSIZE (1024 * 1024 * 10)		 //  10M  
  
typedef struct _NaluUnit
{  
	int type;  
	int size;  
	unsigned char *data;  
}NaluUnit;	
  
typedef struct _RTMPMetadata  
{  
	// video, must be h264 type  
	unsigned int	nWidth;  
	unsigned int	nHeight;  
	unsigned int	nFrameRate; 	// fps	
	unsigned int	nVideoDataRate; // bps	
	unsigned int	nSpsLen;  
	unsigned char	Sps[1024];	
	unsigned int	nPpsLen;  
	unsigned char	Pps[1024];	
  
	// audio, must be aac type	
	bool			bHasAudio;	
	unsigned int	nAudioSampleRate;  
	unsigned int	nAudioSampleSize;  
	unsigned int	nAudioChannels;  
	char			pAudioSpecCfg;	
	unsigned int	nAudioSpecCfgLen;  
  
} RTMPMetadata,*LPRTMPMetadata;  
  
struct RTMP;  
class CRTMPStream  
{  
public:  
	CRTMPStream(void);	
	~CRTMPStream(void);  
public:  
	void SetOUTChunkSize(int size);
	int Connect(const char* url,bool bWrite);
	int Read(unsigned char *buf,int bufferSize,int *plen);
	void Close();
	bool SendMetadata(LPRTMPMetadata lpMetaData);
	bool SendH264Packet(unsigned char *data,unsigned int size,bool bIsKeyFrame,unsigned int nTimeStamp);
	bool OpenH264File(const char *pFileName);
	int SendVideoHeader();
	int SendVideoHeaderRaw(vmc::BYTE *pSPS,int lenSPS,vmc::BYTE *pPPS,int lenPPS);
	int SendVideoFrame();
	int OpenAACFile(const char *sPath);
	int SendAACHeader();
	int SendAACFrame();
	bool ReadOneNaluFromBuf(NaluUnit &nalu);
	unsigned char * GetFileDataBuffer();
	int CurPos();
private:  
	int SendPacket(unsigned int nPacketType,unsigned char *data,unsigned int size,unsigned int nTimestamp);
	
private:  
	RTMP* m_pRtmp;	
	unsigned char* m_pFileBuf;	
	unsigned int  m_nFileBufSize;  
	unsigned int  m_nCurPos;
	FILE *m_fpAAC;
	unsigned long long m_tickStart;
};	

class VTimeStamp
{
	unsigned long long m_timeStart;
public:
	VTimeStamp();
	void Start();
	//void Update();
	unsigned long long TimeMs();
};




